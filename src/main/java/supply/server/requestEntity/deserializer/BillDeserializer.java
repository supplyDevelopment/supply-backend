package supply.server.requestEntity.deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import supply.server.data.utils.company.Bil;

import java.io.IOException;

public class BillDeserializer extends JsonDeserializer<Bil> {

    @Override
    public Bil deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        return new Bil(jsonParser.getValueAsString());
    }
}
