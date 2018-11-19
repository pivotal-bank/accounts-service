package io.pivotal.accounts.repository;

import io.pivotal.accounts.domain.Account;
import io.pivotal.accounts.domain.AccountType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.ws.rs.QueryParam;
import java.util.List;

public interface AccountRepository extends CrudRepository<Account,Integer> {

	@Query("from Account where type = :type")
	List<Account> findByUseridAndType(@Param("type") AccountType type);

	@Query("from Account ")
    List<Account> findByUserid();
}
