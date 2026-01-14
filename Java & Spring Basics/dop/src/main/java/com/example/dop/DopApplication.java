package com.example.dop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DopApplication {

	public static void main(String[] args) {
		SpringApplication.run(DopApplication.class, args);
	}

}

// sealed types
// pattern matching
// records
// smart switch

class Loans {
	String displayMessageFor(Loan loan) {
		return switch (loan) {
			case SecuredLoan _ -> "good job. nice loan";
			case UnsecuredLoan(var interest) -> "you are in trouble " + interest + "% is a lot!";
		};

//		if (loan instanceof SecuredLoan) {
//			var sl = (SecuredLoan) loan;
//			msg = "good job. nice loan";
//		}
//		else if (loan instanceof UnsecuredLoan) {
//			var ul = (UnsecuredLoan) loan;
//			msg = "you are in trouble." + ul.interest() + "% is a lot";
//		}
//		return msg;

	}
}

sealed interface Loan permits SecuredLoan, UnsecuredLoan {

}

final class SecuredLoan implements Loan {

}

record UnsecuredLoan (float interest) implements Loan {

}