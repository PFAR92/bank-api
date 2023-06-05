package com.eprogramar.bankapi

import com.eprogramar.bankapi.model.Account
import com.eprogramar.bankapi.repository.AccountRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.AUTO_CONFIGURED)
class AccountControllerTest {

    @Autowired
    lateinit var accountRepository: AccountRepository

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun `teste find all`() {
        accountRepository.save(Account(name = "Test", document = "05623514658", phone = "93988562536"))

        mockMvc.perform(MockMvcRequestBuilders.get("/accounts"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("\$").isArray)
            .andExpect(MockMvcResultMatchers.jsonPath("\$[0].id").isNumber)
            .andExpect(MockMvcResultMatchers.jsonPath("\$[0].name").isString)
            .andExpect(MockMvcResultMatchers.jsonPath("\$[0].document").isString)
            .andExpect(MockMvcResultMatchers.jsonPath("\$[0].phone").isString)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `teste find by id`() {
        val testAccount = Account(name = "Test", document = "05623514658", phone = "93988562536")
        accountRepository.save(testAccount)

        mockMvc.perform(MockMvcRequestBuilders.get("/accounts/${testAccount.id}"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.id").value(testAccount.id))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.name").value(testAccount.name))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.document").value(testAccount.document))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.phone").value(testAccount.phone))
            .andDo(MockMvcResultHandlers.print())

    }

    @Test
    fun `teste create account`() {
        val testAccount = Account(name = "Test create", document = "05623514658", phone = "93988562536")
        val jason = ObjectMapper().writeValueAsString(testAccount)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/accounts")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jason)
        )
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.name").value(testAccount.name))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.document").value(testAccount.document))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.phone").value(testAccount.phone))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `teste update account`() {
        val testAccount = accountRepository
            .save(Account(name = "Test", document = "05623514658", phone = "93988562536"))
            .copy(name = "update")
        val jason = ObjectMapper().writeValueAsString(testAccount)

        mockMvc.perform(
            (MockMvcRequestBuilders.put("/accounts/${testAccount.id}"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jason)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.name").value(testAccount.name))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.document").value(testAccount.document))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.phone").value(testAccount.phone))
            .andDo(MockMvcResultHandlers.print())

        val findById = accountRepository.findById(testAccount.id!!)
        Assertions.assertTrue(findById.isPresent)
        Assertions.assertEquals(testAccount.name, findById.get().name)

    }

    @Test
    fun `teste delete account`() {
        val testAccount = accountRepository
            .save(Account(name = "Test", document = "05623514658", phone = "93988562536"))

        mockMvc.perform(MockMvcRequestBuilders.delete("/accounts/${testAccount.id}"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(MockMvcResultHandlers.print())

        val findById = accountRepository.findById(testAccount.id!!)
        Assertions.assertFalse(findById.isPresent)
    }

    @Test
    fun `teste create account validation error empty name`() {
        val testAccount = Account(name = "", document = "05623514658", phone = "93988562536")
        val jason = ObjectMapper().writeValueAsString(testAccount)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/accounts")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jason)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.statusCode").isNumber)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.message").isString)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.statusCode").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.message").value("[name] não pode estar em branco"))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `teste create account validation error name should be 5 character`() {
        val testAccount = Account(name = "test", document = "05623514658", phone = "93988562536")
        val jason = ObjectMapper().writeValueAsString(testAccount)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/accounts")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jason)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.statusCode").isNumber)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.message").isString)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.statusCode").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.message").value("[name] deve ter no mínimo 5 caracteres!"))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `teste create account validation error empty document`() {
        val testAccount = Account(name = "test create", document = "", phone = "93988562536")
        val jason = ObjectMapper().writeValueAsString(testAccount)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/accounts")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jason)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.statusCode").isNumber)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.message").isString)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.statusCode").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.message").value("[document] não pode estar em branco"))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `teste create account validation error document should be 11 character`() {
        val testAccount = Account(name = "test create", document = "056235146", phone = "93988562536")
        val jason = ObjectMapper().writeValueAsString(testAccount)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/accounts")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jason)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.statusCode").isNumber)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.message").isString)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.statusCode").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.message").value("[document] deve ter no mínimo 11 caracteres!"))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `teste create account validation error empty phone`() {
        val testAccount = Account(name = "test create", document = "11568954823", phone = "")
        val jason = ObjectMapper().writeValueAsString(testAccount)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/accounts")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jason)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.statusCode").isNumber)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.message").isString)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.statusCode").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.message").value("[phone] não pode estar em branco"))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `teste create account validation error phone should be 11 character`() {
        val testAccount = Account(name = "test create", document = "11556489578", phone = "939885625")
        val jason = ObjectMapper().writeValueAsString(testAccount)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/accounts")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jason)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.statusCode").isNumber)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.message").isString)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.statusCode").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.message").value("[phone] deve ter no mínimo 11 caracteres!"))
            .andDo(MockMvcResultHandlers.print())
    }


}
