package ru.supply.requestEntity.deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import ru.supply.data.utils.Email;

import java.io.IOException;

public class EmailDeserializer extends JsonDeserializer<Email> {
    @Override
    public Email deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        return new Email(jsonParser.getValueAsString());
    }
}
