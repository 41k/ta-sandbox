package root.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BarRepository extends JpaRepository<BarDbEntry, Long>
{
    @Query("SELECT bar FROM BarDbEntry bar WHERE " +
                    "bar.symbol = :symbol AND " +
                    "bar.interval = :interval AND " +
                    "bar.timestamp >= :fromTimestamp AND " +
                    "bar.timestamp <= :toTimestamp")
    List<BarDbEntry> getBarsInTimeRange(String symbol, String interval, Long fromTimestamp, Long toTimestamp);
}
