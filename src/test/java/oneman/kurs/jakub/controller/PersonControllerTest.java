package oneman.kurs.jakub.controller;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class PersonControllerTest {

	@Autowired
	private MockMvc mockMvc;


	@Test
	void checkForPersons() throws Exception {
		mockMvc.perform(get("/persons"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(3)))

				.andExpect(jsonPath("$[*].first_name").exists())
				.andExpect(jsonPath("$[*].last_name").exists())
				.andExpect(jsonPath("$[*].description").exists())
				.andExpect(jsonPath("$[*].date_of_birth").exists())

				.andExpect(jsonPath("$[*].date_of_birth").isNotEmpty())

				.andExpect(jsonPath("$[1].first_name", is("Max")))
				.andExpect(jsonPath("$[1].last_name", is("Mustermann")))
				.andExpect(jsonPath("$[1].date_of_birth", is("1997-06-23")));
	}

	@Test
	void postInvalidPerson() throws Exception {
		testInvalidPerson("{}");
		testInvalidPerson("{ \"first_name\": null }");
		testInvalidPerson("{ \"last_name\": null }");
		testInvalidPerson("{ \"first_name\": \"\" }");
		testInvalidPerson("{ \"last_name\": \"\" }");
		testInvalidPerson("{ \"first_name\": \"A\" }");
		testInvalidPerson("{ \"first_name\": \"A very long first name that exceeds the limit\" }");
		testInvalidPerson("{ \"first_name\": \"John!@#\" }");
		testInvalidPerson("{ \"last_name\": \"Doe!@#\" }");
		testInvalidPerson("{ \"birth_of_date\": \"31-12-1991\" }");
		testInvalidPerson("{ \"birth_of_date\": \"1991/12/31\" }");
		testInvalidPerson("{ \"first_name\": \"John\", \"unknown_field\": \"value\" }");
		testInvalidPerson("{ \"first_name\": \"John\", \"last_name\": \"Doe\", \"extra_field\": \"value\" }");
		testInvalidPerson("{ \"first_name\": \"John\" }");
		testInvalidPerson("{ \"first_name\": null, \"last_name\": \"Doe\" }");
		testInvalidPerson("{ \"first_name\": \"John\", \"last_name\": null }");
		testInvalidPerson("{ \"first_name\": \"John \"Doe\"\" }");
		testInvalidPerson("{ \"birth_of_date\": \"2025-02-30\" }");
		testInvalidPerson("{ \"birth_of_date\": \"9999-12-31\" }");
		testInvalidPerson("{ \"birth_of_date\": \"0001-01-01\" }");
		testInvalidPerson("{ \"birth_of_date\": \"text\" }");
		testInvalidPerson("{ \"first_name\": 123 }");
		testInvalidPerson("{ \"first_name\": \"123\", \"last_name\": \"456\" }");
		testInvalidPerson("{ \"first_name\": \"John\", \"last_name\": \"Doe\" ");
		testInvalidPerson("{ \"first_name\": \"John\" , \"last_name\": \"Doe\", }");
		testInvalidPerson("{ \"first_name\": \"John \"Doe\"\" }");
	}

	private void testInvalidPerson(String input) throws Exception {
		mockMvc.perform(post("/persons")
						.content(input)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$", hasSize(3)))
				.andExpect(status().isBadRequest());
	}

	@Test
	void postValidPerson () throws Exception {
		String correctInputComplete = """
				{
					"first_name": "Jakub",
					"last_name": "Waclawowicz"
				}
				""";

		String response = mockMvc.perform(post("/persons")
						.content(correctInputComplete)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andReturn()
				.getResponse()
				.getContentAsString();

		String personId = JsonPath.read(response, "$.id");
		assertTrue(personId != null && !personId.isEmpty(), "Person ID should be returned");

		String personFilterPath = "$[?(@.id == '" + personId + "')][0]";

		mockMvc.perform(get("/persons"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(4)))

				.andExpect(jsonPath(personFilterPath + ".id", is(personId)))
				.andExpect(jsonPath(personFilterPath + ".first_name", is("Jakub")))
				.andExpect(jsonPath(personFilterPath + ".last_name", is("Waclawowicz")));

		String addBirthday = """
				{
					"first_name": "Jakub",
					"last_name": "Waclawowicz",
					"birth_of_date": "1991-07-30"
				}
				""";

		mockMvc.perform(put("/persons/" + personId)
						.content(addBirthday)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		mockMvc.perform(get("/persons"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(4)))

				.andExpect(jsonPath(personFilterPath + ".id", is(personId)))
				.andExpect(jsonPath(personFilterPath + ".first_name", is("Jakub")))
				.andExpect(jsonPath(personFilterPath + ".last_name", is("Waclawowicz")))
				.andExpect(jsonPath(personFilterPath + ".birth_of_date", is("1991-07-30")));
	}
}
