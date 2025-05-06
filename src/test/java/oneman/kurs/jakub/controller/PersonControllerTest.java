package oneman.kurs.jakub.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

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

				.andExpect(jsonPath("$[1].first_name").is("Max"))
				.andExpect(jsonPath("$[1].last_name").is("Mustermann"))
				.andExpect(jsonPath("$[1].date_of_birth").is("1997-06-23"));
	}
}
