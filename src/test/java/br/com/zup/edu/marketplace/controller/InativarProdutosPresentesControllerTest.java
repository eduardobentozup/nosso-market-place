package br.com.zup.edu.marketplace.controller;

import br.com.zup.edu.marketplace.model.Produto;
import br.com.zup.edu.marketplace.model.StatusProduto;
import br.com.zup.edu.marketplace.repository.ProdutoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
class InativarProdutosPresentesControllerTest {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private ProdutoRepository repository;

	@BeforeEach
	void setUp() {
		repository.deleteAll();
	}

	@Test
	@DisplayName("Should inactivate a product")
	void shouldInactivateProduct() throws Exception {

		Produto luminary = new Produto(
			"Luminária",
			"Para iluminação de escrivaninhas",
			new BigDecimal(90.5)
		);

		repository.save(luminary);

		MockHttpServletRequestBuilder request = post("/produtos/inativar");

		String responseJson = mvc.perform(request)
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString(StandardCharsets.UTF_8);

		List<Long> ids = Arrays.asList(mapper.readValue(responseJson, Long[].class));

		assertTrue(ids.contains(luminary.getId()));

		Optional<Produto> possibleLuminary= repository.findById(luminary.getId());

		assertTrue(possibleLuminary.isPresent());
		assertEquals(StatusProduto.INATIVO, possibleLuminary.get().getStatus());
	}

}