package cn.wolfcode.shop.mapper;

import cn.wolfcode.shop.domain.Catalog;
import java.util.List;

public interface CatalogMapper {
    void deleteByPrimaryKey(Long id);

    int insert(Catalog record);

    Catalog selectByPrimaryKey(Long id);

    List<Catalog> selectAll();

    int updateByPrimaryKey(Catalog record);

    List<Catalog> getChildCatalog(Long catalogId);

    Integer propertyCount(Long catalogId);

    Integer productCount(Long id);

    List<Catalog> getParentCatalog(Long catalogId);
}