package Interfaces;

import entities.BaseEntity.BaseEntity;

import java.util.List;

public interface OwnEntity
{
    public List<? extends BaseEntity> getEntity();

}
