package com.leslie.framework.ieasyexcel.example.repository;

import com.leslie.framework.ieasyexcel.example.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author leslie
 * @date 2021/6/07
 */
@Repository
public interface CityRepository extends JpaRepository<City,String> {
}
