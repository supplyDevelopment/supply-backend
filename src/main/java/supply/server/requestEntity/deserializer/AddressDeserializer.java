package supply.server.requestEntity.deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import supply.server.data.utils.Address;

import java.io.IOException;

public class AddressDeserializer extends JsonDeserializer<Address> {

    @Override
    public Address deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        return new Address(jsonParser.getValueAsString());
    }

}
