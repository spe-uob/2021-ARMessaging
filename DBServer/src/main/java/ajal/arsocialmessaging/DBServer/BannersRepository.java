package ajal.arsocialmessaging.DBServer;

import org.springframework.data.repository.CrudRepository;

import java.sql.Timestamp;

public interface BannersRepository extends CrudRepository<Banner, Integer>{
    long deleteByPostcodeAndTimestamp(String postcode, Timestamp timestamp);
}
