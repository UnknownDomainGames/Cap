package main;

import org.junit.Assert;
import org.junit.Test;

import unknowndomain.permission.Permissible;
import unknowndomain.permission.hash.HashPermissible;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HashPermissibleTest {

    @Test
    public void permissibleTest() {
        Permissible permissible = new HashPermissible();
        List<String> truePermissions = getRandomPermissions();
        List<String> falsePermissions = getRandomPermissions();

        truePermissions.forEach(permission->permissible.setPermission(permission,true));

        Thread thread = new Thread(()-> falsePermissions.forEach(permission->permissible.setPermission(permission,false)));
        thread.start();

        truePermissions.forEach(permission->Assert.assertEquals(permissible.hasPermission(permission),true));

        List<String> parentTestPermissions = getRandomPermissions();
        Permissible permissible2 = new HashPermissible();
        parentTestPermissions.stream().filter(s->s.indexOf('.')>0).forEach(s->permissible2.setPermission(s.substring(0,s.indexOf('.')),true));
        parentTestPermissions.stream().filter(s->s.indexOf('.')>0).forEach(s->Assert.assertEquals(permissible2.hasPermission(s),true));
        parentTestPermissions.stream().filter(s->s.indexOf('.')<=0).forEach(s->Assert.assertEquals(permissible2.hasPermission(s),false));
    }

    private List<String> getRandomPermissions(){
        ArrayList<String> permission = new ArrayList<>();
        Random random = new Random(System.currentTimeMillis());
        for(int i = 0;i<100;i++)
            permission.add(new Double(random.nextDouble()).toString()+new Double(random.nextDouble()).toString());
        return permission;
    }
}
