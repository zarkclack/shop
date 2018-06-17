package cn.wolfcode.shop.service;

import cn.wolfcode.shop.domain.Catalog;

import java.util.List;


public interface ICatalogService {
    void delete(Long id);

    Catalog get(Long id);

    List<Catalog> selectAll();

    void saveOrUpdate(Catalog record);

    List<Catalog> getChildCatalog(Long catalogId);

    String getAllCatalogByCache();

    void catalogSort(List<Long> ids);
}
