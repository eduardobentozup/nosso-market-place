package br.com.zup.edu.marketplace.controller;

import br.com.zup.edu.marketplace.controller.response.ProdutoResponse;
import br.com.zup.edu.marketplace.model.Produto;
import br.com.zup.edu.marketplace.repository.ProdutoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
class ListarProdutosControllerTest {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private ProdutoRepository produtoRepository;

	private final String URL = "/produtos";

	@BeforeEach
	void setUp() {
		produtoRepository.deleteAll();
	}

	@Test
	@DisplayName("Should list products")
	void shouldListProducts() throws Exception{

		Produto notebook = new Produto("Notebook", "Portable computer", new BigDecimal(3000));

		produtoRepository.save(notebook);

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(URL)
			.header("Accept-Language", "pt-br")
			.contentType(MediaType.APPLICATION_JSON);

		String responseJson = mvc.perform(request)
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString(StandardCharsets.UTF_8);

		TypeFactory typeFactory = mapper.getTypeFactory();

		List<ProdutoResponse> response = mapper.readValue(
			responseJson,
			typeFactory.constructCollectionType(List.class, ProdutoResponse.class)
		);

		assertThat(response)
			.hasSize(1)
			.extracting("titulo", "descricao", "preco")
			.contains(
				new Tuple(
					"Notebook",
					"Portable computer",
					new BigDecimal(3000).setScale(2, RoundingMode.HALF_EVEN)
				)
			);
	}

	@Test
	@DisplayName("Should return empty list")
	void shouldReturnEmptyList() throws Exception{

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(URL)
			.header("Accept-Language", "pt-br")
			.contentType(MediaType.APPLICATION_JSON);

		String responseJson = mvc.perform(request)
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString(StandardCharsets.UTF_8);

		TypeFactory typeFactory = mapper.getTypeFactory();

		List<ProdutoResponse> response = mapper.readValue(
			responseJson,
			typeFactory.constructCollectionType(List.class, ProdutoResponse.class)
		);

		assertTrue(response.isEmpty());
	}
}