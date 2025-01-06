package supply.server.requestEntity.deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import supply.server.data.utils.company.CompanyStatus;

import java.io.IOException;

public class CompanyStatusDeserializer extends JsonDeserializer<CompanyStatus> {

    @Override
    public CompanyStatus deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        return CompanyStatus.of(jsonParser.getValueAsString());
    }

}
