package carPartsStore.db;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

//CrudRepository<CarMake, Long>, PagingAndSortingRepository<CarMake, Long>
//JpaRepository<CarMake, Long>
public interface CarMakeRepository extends CrudRepository<CarMake, Long>, PagingAndSortingRepository<CarMake, Long> {
}
