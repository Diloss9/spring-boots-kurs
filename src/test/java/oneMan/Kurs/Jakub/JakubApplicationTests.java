package oneMan.Kurs.Jakub;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
class JakubApplicationTests {

	@Autowired
	private MockMvc mockMvc;


	@Test
	void contextLoads() throws Exception {
		this.mockMvc.perform(get("/persons"))
				.andExpect(status().isOk());
	}

}
