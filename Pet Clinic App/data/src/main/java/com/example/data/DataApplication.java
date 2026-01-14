package com.example.data;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.annotation.Id;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Set;

@SpringBootApplication
public class DataApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataApplication.class, args);
	}

	@Bean
	ApplicationRunner runner(PersonRepository repository) {
		return _ ->
				repository.findByName("jlong").forEach((System.out::println));
//				repository.findAll().forEach(System.out::println);
	}
}
record Dog(int id, String name, String owner, String description) {}
record Person(String name, @Id int id, Set<Dog> dogs) {

}

//@Repository
//class WrongWayPersonRepository implements PersonRepository {
//
//	private final JdbcClient db;
//
//	WrongWayPersonRepository(JdbcClient db) {
//		this.db = db;
//	}
//
//	@Override
//	public Collection<Person> findAll() {
//		return db.sql( "select * from person" )
//				.query((rs, rowNum) ->
//						new Person(rs.getString("name"), rs.getInt("id")))
//				.list();
//	}
//}

interface PersonRepository extends ListCrudRepository <Person, Integer> {

//	Collection<Person> findAll();
	@Query ("select * from person p where p.name = :name ") // for more controls
	Collection <Person> findByName(String name);
}
