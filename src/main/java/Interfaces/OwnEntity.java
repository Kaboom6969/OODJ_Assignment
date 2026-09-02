package Interfaces;

import entities.BaseEntity.BaseEntity;
import entities.LazyEntity.LazyEntity;

import java.util.List;

public interface OwnEntity extends OwnerShip
{
    public List<LazyEntity<? extends BaseEntity>> getEntity();

}
