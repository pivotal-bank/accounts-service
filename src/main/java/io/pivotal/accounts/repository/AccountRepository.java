package io.pivotal.accounts.repository;

import io.pivotal.accounts.domain.Account;
import io.pivotal.accounts.domain.AccountType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AccountRepository extends CrudRepository<Account,Integer> {

	@Query("from Account where type = :type and userid = ?#{principal.claims['user_id']}")
	List<Account> findByUseridAndType(@Param("type") AccountType type);

	@Query("from Account where userid = ?#{principal.claims['user_id']}")
    List<Account> findByUserid();
}
