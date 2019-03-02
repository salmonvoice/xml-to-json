package software.engineering.task.data;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;


@Repository
public interface ExchangeRatesRepository extends CrudRepository<ExchangeRates, String> {}
