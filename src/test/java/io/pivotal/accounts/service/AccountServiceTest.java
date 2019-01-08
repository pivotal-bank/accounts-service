package io.pivotal.accounts.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.isA;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.pivotal.accounts.configuration.ServiceTestConfiguration;
import io.pivotal.accounts.domain.Account;
import io.pivotal.accounts.domain.AccountType;
import io.pivotal.accounts.exception.AuthenticationException;
import io.pivotal.accounts.exception.NoRecordsFoundException;
import io.pivotal.accounts.repository.AccountRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
/**
 * Tests for the AccountService.
 * 
 * @author David Ferreira Pinto
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class AccountServiceTest {

	@InjectMocks
	AccountService service;
	
	@Mock
	AccountRepository repo;


	/**
	 * test retrieval of account by integer.
	 */
	@Test
	public void doFindAccount() {
		when(repo.findById(ServiceTestConfiguration.PROFILE_ID)).thenReturn(Optional.of(ServiceTestConfiguration.account()));
		assertEquals(service.findAccount(ServiceTestConfiguration.PROFILE_ID).toString(),ServiceTestConfiguration.account().toString());
	}
	/**
	 * test retrieval of account by string - userid.
	 */
	@Test
	public void doFindAccountUserId() {
		when(repo.findByUserid()).thenReturn(ServiceTestConfiguration.accountList());
		List<Account> accounts = service.findAccounts();
		assertEquals(accounts.size(),1);
		assertEquals(accounts.get(0), ServiceTestConfiguration.account());
	}
	/**
	 * test retrieval of account by string - userid, with no account found.
	 */
	@Test
	public void doFindAccountUserIdNotFound() {
		when(repo.findByUserid()).thenReturn(new ArrayList());
		List<Account> accounts = service.findAccounts();
		assertEquals(accounts.size(),0);
	}
	
	/**
	 * test retrieval of account by userid and type.
	 */
	@Test
	public void doFindAccountsByType() {
		when(repo.findByUseridAndType(AccountType.CURRENT)).thenReturn(ServiceTestConfiguration.accountList());
		List<Account> accounts = service.findAccountsByType(AccountType.CURRENT);
		assertEquals(accounts.size(),1);
		assertEquals(accounts.get(0),ServiceTestConfiguration.account());
	}
	
	/**
	 * test retrieval of account not found.
	 */
	@Test(expected=NoRecordsFoundException.class)
	public void doFindNullAccount() {
		when(repo.findById(999)).thenReturn(Optional.empty());
		service.findAccount(999);
	}
	
	/**
	 * test saving of account.
	 */
	@Test
	public void saveAccount() {
		Account acc = ServiceTestConfiguration.account();
		when(repo.save(acc)).thenReturn(acc);
		assertEquals(service.saveAccount(acc),acc.getId());
	}

	
	/**
	 * Test Account domain object hashcode.
	 */
	@Test
	public void testAccountObject() {
		Account acc1 = ServiceTestConfiguration.account();
		Account acc2 = ServiceTestConfiguration.account();
		
		assertEquals(acc1.hashCode(),acc2.hashCode());
	}
}
