package io.pivotal.accounts.controller;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.pivotal.accounts.configuration.ServiceTestConfiguration;
import io.pivotal.accounts.domain.AccountType;
import io.pivotal.accounts.service.AccountService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests for the AccountsController.
 *
 * @author David Ferreira Pinto
 */

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = AccountController.class, secure = false)
public class AccountControllerTest {

    private static final String EXPECTED_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'+0000'";

    @Autowired
    private MockMvc mockMvc;


    @MockBean
    private AccountService service;


    /**
     * Test the POST to <code>/account</code>.
     * test creation of accounts.
     *
     * @throws Exception
     */
    @Test
    public void doPostAccount() throws Exception {
        when(service.saveAccount(ServiceTestConfiguration.account()))
                .thenReturn(ServiceTestConfiguration.ACCOUNT_ID);

        mockMvc.perform(
                post("/accounts").contentType(MediaType.APPLICATION_JSON)
                        .content(
                                convertObjectToJson(ServiceTestConfiguration
                                        .account())))
                .andExpect(status().isCreated()).andDo(print());
    }

    /**
     * Test the GET to <code>/account</code>.
     * test retrieval of accounts.
     *
     * @throws Exception
     */
    @Test
    public void doGetAccount() throws Exception {
        when(service.findAccount(ServiceTestConfiguration.ACCOUNT_ID))
                .thenReturn(ServiceTestConfiguration.account());

        mockMvc.perform(
                get("/accounts/" + ServiceTestConfiguration.ACCOUNT_ID)
                        .contentType(MediaType.APPLICATION_JSON).content(
                        convertObjectToJson(ServiceTestConfiguration
                                .account())))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(
                        content().contentTypeCompatibleWith(
                                MediaType.APPLICATION_JSON))
                .andExpect(
                        jsonPath("$.id").value(
                                ServiceTestConfiguration.ACCOUNT_ID))
                .andExpect(
                        jsonPath("$.creationdate").value(
                                ServiceTestConfiguration.ACCOUNT_DATE.toString(EXPECTED_DATE_FORMAT)))
                .andExpect(
                        jsonPath("$.openbalance").value(
                                ServiceTestConfiguration.ACCOUNT_OPEN_BALANCE
                                        .doubleValue()))
                .andExpect(
                        jsonPath("$.balance").value(
                                ServiceTestConfiguration.ACCOUNT_BALANCE))
                .andDo(print());
    }

    /**
     * Test the GET to <code>/accounts</code>.
     * test retrieval of accounts by username.
     *
     * @throws Exception
     */
    @Test
    public void doGetAccounts() throws Exception {
        when(service.findAccounts())
                .thenReturn(Collections.singletonList(ServiceTestConfiguration.account()));

        mockMvc.perform(
                get("/accounts?name=" + ServiceTestConfiguration.USER_ID)
                        .contentType(MediaType.APPLICATION_JSON).content(
                        convertObjectToJson(ServiceTestConfiguration
                                .account())))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(
                        content().contentTypeCompatibleWith(
                                MediaType.APPLICATION_JSON))
                .andExpect(
                        jsonPath("$[0].id").value(
                                ServiceTestConfiguration.ACCOUNT_ID))
                .andExpect(
                        jsonPath("$[0].creationdate").value(
                                ServiceTestConfiguration.ACCOUNT_DATE.toString(EXPECTED_DATE_FORMAT)))
                .andExpect(
                        jsonPath("$[0].openbalance").value(
                                ServiceTestConfiguration.ACCOUNT_OPEN_BALANCE
                                        .doubleValue()))
                .andExpect(
                        jsonPath("$[0].balance").value(
                                ServiceTestConfiguration.ACCOUNT_BALANCE))
                .andDo(print());
    }

    /**
     * Test the GET to <code>/accounts</code>.
     * test retrieval of accounts by username and type.
     *
     * @throws Exception
     */
    @Test
    public void doGetAccountsWithType() throws Exception {
        when(service.findAccountsByType(AccountType.CURRENT))
                .thenReturn(Collections.singletonList(ServiceTestConfiguration.account()));

        mockMvc.perform(
                get("/accounts")
                        .param("name", ServiceTestConfiguration.USER_ID)
                        .param("type", "CURRENT")
                        .contentType(MediaType.APPLICATION_JSON).content(
                        convertObjectToJson(ServiceTestConfiguration
                                .account())))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(
                        content().contentTypeCompatibleWith(
                                MediaType.APPLICATION_JSON))
                .andExpect(
                        jsonPath("$[0].id").value(
                                ServiceTestConfiguration.ACCOUNT_ID))
                .andExpect(
                        jsonPath("$[0].creationdate").value(
                                ServiceTestConfiguration.ACCOUNT_DATE.toString(EXPECTED_DATE_FORMAT)))
                .andExpect(
                        jsonPath("$[0].openbalance").value(
                                ServiceTestConfiguration.ACCOUNT_OPEN_BALANCE
                                        .doubleValue()))
                .andExpect(
                        jsonPath("$[0].balance").value(
                                ServiceTestConfiguration.ACCOUNT_BALANCE))
                .andDo(print());
    }

    /**
     * Test the GET to <code>/accounts/transaction/</code>.
     * test increase of balance.
     *
     * @throws Exception
     */
    @Test
    public void doIncreaseBalance() throws Exception {
        when(service.findAccount(ServiceTestConfiguration.ACCOUNT_ID))
                .thenReturn(ServiceTestConfiguration.account());

        MvcResult result = mockMvc.perform(
                post("/accounts/transaction")
                        .contentType(MediaType.APPLICATION_JSON).content(
                        convertObjectToJson(ServiceTestConfiguration
                                .getCreditTransaction())))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().string("SUCCESS"))
                .andReturn();
    }

    /**
     * Test the GET to <code>/account/transaction</code>.
     * test increase of balance with negative amount.
     *
     * @throws Exception
     */
    @Test
    public void doIncreaseBalanceNegative() throws Exception {
        when(service.findAccount(ServiceTestConfiguration.ACCOUNT_ID))
                .thenReturn(ServiceTestConfiguration.account());

        MvcResult result = mockMvc.perform(
                post("/accounts/transaction")
                        .contentType(MediaType.APPLICATION_JSON).content(
                        convertObjectToJson(ServiceTestConfiguration
                                .getBadCreditTransaction())))
                .andExpect(status().isExpectationFailed())
                .andDo(print())
                .andExpect(content().string("FAILED"))
                .andReturn();
    }

    /**
     * Test the GET to <code>/account/transaction</code>.
     * test decrease of balance.
     *
     * @throws Exception
     */
    @Test
    public void doDecreaseBalance() throws Exception {
        when(service.findAccount(ServiceTestConfiguration.ACCOUNT_ID))
                .thenReturn(ServiceTestConfiguration.account());

        mockMvc.perform(
                post("/accounts/transaction")
                        .contentType(MediaType.APPLICATION_JSON).content(
                        convertObjectToJson(ServiceTestConfiguration
                                .getDebitTransaction())))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().string("SUCCESS"))
                .andDo(print());
    }

    /**
     * Test the GET to <code>/account/transaction</code>.
     * test decrease of balance with not enough funds.
     *
     * @throws Exception
     */

    @Test
    public void doDecreaseBalanceNoFunds() throws Exception {
        when(service.findAccount(ServiceTestConfiguration.ACCOUNT_ID))
                .thenReturn(ServiceTestConfiguration.account());

        mockMvc.perform(
                post("/accounts/transaction")
                        .contentType(MediaType.APPLICATION_JSON).content(
                        convertObjectToJson(ServiceTestConfiguration
                                .getBadDebitTransaction())))
                .andExpect(status().isExpectationFailed())
                .andDo(print())
                .andExpect(content().string("FAILED"))
                .andDo(print());
    }

    private String convertObjectToJson(Object request) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(Include.NON_NULL);
        return mapper.writeValueAsString(request);
    }
}
