package com.example.siidemo;

import com.example.siidemo.api.dto.TransactionRequest;
import com.example.siidemo.persistence.entity.Transaction;
import com.example.siidemo.persistence.entity.TransactionData;
import com.example.siidemo.persistence.repository.TransactionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TransactionControllerIntegrationTest {

    private static final String BASE = "/api/transaction";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Replace only the JPA repository with a Mockito mock so running tests does not rely on DB.
     * All other beans (service, mapper, controller) are real.
     */
    @MockitoBean
    private TransactionRepository transactionRepository;

    @BeforeEach
    void resetMocks() {
        reset(transactionRepository);
    }

    @Test
    @DisplayName("POST /api/transaction → 204 No Content, and service saves entity")
    void createTransaction() throws Exception {
        TransactionRequest req = new TransactionRequest(
                123,
                OffsetDateTime.parse("2025-05-07T10:15:30+02:00"),
                "PAYMENT",
                "alice",
                Map.of("foo","bar")
        );

        mockMvc.perform(post(BASE)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNoContent());

        // verify that our service wrote to the repository
        verify(transactionRepository).save(argThat(tx ->
                tx.getForeignId() == 123 &&
                        tx.getType().equals("PAYMENT") &&
                        tx.getActor().equals("alice") &&
                        tx.getTransactionData().stream()
                                .anyMatch(d -> "foo".equals(d.getDataKey()) && "bar".equals(d.getDataValue()))
        ));
    }

    @Test
    @DisplayName("GET /api/transaction/{id} → 200 OK + JSON body")
    void readTransaction() throws Exception {
        // prepare a fake Transaction entity
        Transaction tx = new Transaction();
        tx.setForeignId(123);
        tx.setTimestamp(OffsetDateTime.parse("2025-05-07T10:15:30+02:00"));
        tx.setType("PAYMENT");
        tx.setActor("alice");
        TransactionData d = new TransactionData();
        d.setDataKey("foo");
        d.setDataValue("bar");
        d.setTransaction(tx);
        tx.setTransactionData(Set.of(d));

        when(transactionRepository.getTransactionsByForeignId(123))
                .thenReturn(List.of(tx));

        mockMvc.perform(get(BASE + "/{id}", 123)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transaction.id").value(123))
                .andExpect(jsonPath("$.transaction.type").value("PAYMENT"))
                .andExpect(jsonPath("$.transaction.actor").value("alice"))
                // <-- updated for Map<String,String> serialization
                .andExpect(jsonPath("$.transaction.transactionData.foo").value("bar"));
    }

    @Test
    @DisplayName("PUT /api/transaction/{id} → 204 No Content")
    void updateTransaction() throws Exception {
        // stub read so update finds something
        Transaction existing = new Transaction();
        existing.setForeignId(123);
        existing.setTimestamp(OffsetDateTime.now());
        existing.setType("OLD");
        existing.setActor("bob");
        existing.setTransactionData(Set.of());
        when(transactionRepository.getTransactionsByForeignId(123))
                .thenReturn(List.of(existing));

        TransactionRequest req = new TransactionRequest(
                123,
                OffsetDateTime.parse("2025-06-01T12:00:00+02:00"),
                "REFUND",
                "bob",
                Map.of("amount","100")
        );

        mockMvc.perform(put(BASE + "/{id}", 123)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNoContent());

        verify(transactionRepository).save(argThat(tx ->
                tx.getType().equals("REFUND") &&
                        tx.getActor().equals("bob") &&
                        tx.getTransactionData().stream()
                                .anyMatch(d -> "amount".equals(d.getDataKey()) && "100".equals(d.getDataValue()))
        ));
    }

    @Test
    @DisplayName("DELETE /api/transaction/{id} → 204 No Content")
    void deleteTransaction() throws Exception {
        Transaction existing = new Transaction();
        existing.setForeignId(123);
        existing.setTimestamp(OffsetDateTime.now());
        existing.setType("X");
        existing.setActor("y");
        existing.setTransactionData(Set.of());
        when(transactionRepository.getTransactionsByForeignId(123))
                .thenReturn(List.of(existing));

        mockMvc.perform(delete(BASE + "/{id}", 123))
                .andExpect(status().isNoContent());

        verify(transactionRepository).delete(existing);
    }

    @Test
    @DisplayName("GET /api/transaction?… → 200 OK + search results")
    void searchTransactions() throws Exception {
        OffsetDateTime from = OffsetDateTime.parse("2025-01-01T00:00:00Z");
        OffsetDateTime until = OffsetDateTime.parse("2025-12-31T23:59:59Z");

        // build a Transaction with one data-entry foo=bar
        Transaction tx = new Transaction();
        tx.setForeignId(123);
        tx.setTimestamp(OffsetDateTime.parse("2025-07-15T09:30:00+02:00"));
        tx.setType("PAYMENT");
        tx.setActor("alice");

        TransactionData d = new TransactionData();
        d.setDataKey("foo");
        d.setDataValue("bar");
        d.setTransaction(tx);

        tx.setTransactionData(Set.of(d));

        when(transactionRepository.findAll(any(Specification.class)))
                .thenReturn(List.of(tx));

        mockMvc.perform(get(BASE)
                        .param("id", "123")
                        .param("timestampFrom", from.toString())
                        .param("timestampUntil", until.toString())
                        .param("type", "PAYMENT")
                        .param("actor", "alice")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactions[0].id").value(123))
                .andExpect(jsonPath("$.transactions[0].type").value("PAYMENT"))
                .andExpect(jsonPath("$.transactions[0].actor").value("alice"))
                .andExpect(jsonPath("$.transactions[0].transactionData.foo").value("bar"));
    }

}
