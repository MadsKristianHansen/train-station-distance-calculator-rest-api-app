package com.db.trainstationdistancecalculatorrestapiapp;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface StationDataRepository extends CrudRepository<StationData,Integer> {

    Optional<StationData> findByDs100(String ds100);

}
