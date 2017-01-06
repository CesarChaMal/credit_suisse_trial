package hello;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

public class PersonItemProcessor implements ItemProcessor<Person, Person> {

    private static final Logger log = LoggerFactory.getLogger(PersonItemProcessor.class);

    @Override
    public Person process(final Person person) throws Exception {
        final String instrumentName = person.getInstrumentName().toUpperCase();
        final Double value = person.getValue();

        final Person transformedPerson = new Person(instrumentName, value);

        log.info("Converting (" + person + ") into (" + transformedPerson + ")");

        return transformedPerson;
    }


    
}
