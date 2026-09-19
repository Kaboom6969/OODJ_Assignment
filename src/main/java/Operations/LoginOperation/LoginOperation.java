package Operations.LoginOperation;

import Tools.EntityConvertManager;import Tools.HospitalEntityAllocator;import entities.BaseEntity.Users.User;import entities.BusinessEntity.BusinessEntity;

public class LoginOperation
{
    private HospitalEntityAllocator hospitalEntityAllocator;

    public LoginOperation(HospitalEntityAllocator hospitalEntityAllocator)
    {
        this.hospitalEntityAllocator = hospitalEntityAllocator;
    }
    public BusinessEntity<?> login(String username, String password,Class<? extends User> userClass)
    {
        var users = hospitalEntityAllocator.getAllBusinessEntities(EntityConvertManager.getPrefixMap().get(userClass));
        for (BusinessEntity<?> userBusiness : users)
        {
            User user = userClass.cast(userBusiness.getSelf());
            if (user.getName().equals(username) && user.getPassword().equals(password))
            {
                return userBusiness;
            }
        }
        return null;
    }
}
