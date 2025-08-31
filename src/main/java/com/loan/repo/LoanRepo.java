package com.loan.repo;

import com.loan.entity.Loan;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanRepo extends ReactiveCrudRepository<Loan, Integer> {

}
