package ru.supply.requestEntity.deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import ru.supply.data.utils.company.Tax;

import java.io.IOException;

public class TaxDeserializer extends JsonDeserializer<Tax> {

    @Override
    public Tax deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        return new Tax(jsonParser.getValueAsString());
    }
}
