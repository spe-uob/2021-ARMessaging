package ajal.arsocialmessaging.DBServer;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.sql.Timestamp;

public interface BannersRepository extends CrudRepository<Banner, Integer>{

    @Modifying
    @Query("DELETE FROM Banner b WHERE b.postcode = :postcode AND b.timestamp = :timestamp")
    @Transactional
    void deleteByPostcodeAndTimestamp(@Param("postcode") String postcode, @Param("timestamp") Timestamp timestamp);
}

