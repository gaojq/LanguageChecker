package JsonConvertor;
import GrammarCheck.BuildRule;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ConvertToJson {
	public ConvertToJson(BuildRule rule) throws IOException{
		ObjectMapper mapper = new ObjectMapper();
		String uniJSON = mapper.writeValueAsString(rule);
		mapper.writeValue(new File("uni-json.json"), rule);
	}
}
