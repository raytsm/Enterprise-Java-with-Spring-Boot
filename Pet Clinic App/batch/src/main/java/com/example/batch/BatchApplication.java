package com.example.batch;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.parameters.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.UnexpectedInputException;
import org.springframework.batch.infrastructure.item.database.ItemPreparedStatementSetter;
import org.springframework.batch.infrastructure.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.infrastructure.item.file.mapping.FieldSetMapper;
import org.springframework.batch.infrastructure.item.file.transform.FieldSet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.net.BindException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@SpringBootApplication
public class BatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(BatchApplication.class, args);
	}

	record Dog (int id, String name, String owner, String description) {}

	@Bean
	ItemReader<Dog> itemReader (@Value("file://${HOME}/Desktop/dogs.csv") Resource resource) {
		return new FlatFileItemReaderBuilder<Dog>()
				.resource(resource)
				.name("dogReader")
				.delimited().names("person,id,name,description,dob,owner,gender,image".split(","))
				.linesToSkip(1)
				.fieldSetMapper( new FieldSetMapper<Dog>() {
					@Override
					public Dog mapFieldSet(FieldSet fieldSet) {
						return new Dog (fieldSet.readInt("id"),
								fieldSet.readString("name"),
								fieldSet.readString("owner"),
								fieldSet.readString("description")
								);
					}
				})
				.build();
	}

	@Bean
	ItemWriter<Dog> itemWriter (DataSource dataSource) {
		return new JdbcBatchItemWriterBuilder<Dog>()
				.dataSource(dataSource)
				.sql("INSERT INTO DOG (id, name, description, owner) VALUES (?,?,?,?)")
				.assertUpdates(true)
				.itemPreparedStatementSetter(new ItemPreparedStatementSetter<Dog>() {
					@Override
					public void setValues(Dog item, PreparedStatement ps) throws SQLException {
						ps.setInt(1, item.id());
						ps.setString(2, item.name());
						ps.setString(3, item.description());
						ps.setString(4, item.owner());
					}
				})
				.build();
//		return new ItemWriter<Dog>() {
//			@Override
//            public void write(Chunk<? extends Dog> chunk) throws Exception {
//				System.out.println("-----");
//				chunk.forEach(System.out::println);
//				System.out.println("=====");
//			}
//		};
	}

	@Bean
	Step step (JobRepository repository, PlatformTransactionManager transactionManager, ItemReader<Dog> reader, ItemWriter<Dog> writer) {
		return new StepBuilder("step", repository)
				.<Dog, Dog>chunk(10, transactionManager)
				.reader(reader)
				.writer(writer)
				.build();
	}

	@Bean
	Job job(JobRepository repository, Step step) {
		return new JobBuilder("job", repository)
				.incrementer(new RunIdIncrementer())
				.start(step)
				.build();
	}
}
