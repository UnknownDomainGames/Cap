package main;

import org.junit.Assert;
import org.junit.Test;

import unknowndomain.permission.Permissible;
import unknowndomain.permission.hash.PermissibleHash;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PermissibleHashTest {

    @Test
    public void permissibleTest() {
        Permissible permissible = new PermissibleHash();
        List<String> truePermissions = getRandomPermissions();
        List<String> falsePermissions = getRandomPermissions();

        truePermissions.forEach(permission->permissible.definePermission(permission,true));

        Thread thread = new Thread(()-> falsePermissions.forEach(permission->permissible.definePermission(permission,false)));
        thread.start();

        truePermissions.forEach(permission->Assert.assertEquals(permissible.hasPermission(permission),true));

        List<String> parentTestPermissions = getRandomPermissions();
        Permissible permissible2 = new PermissibleHash();
        parentTestPermissions.stream().filter(s->s.indexOf('.')>0).forEach(s->permissible2.definePermission(s,true));
        parentTestPermissions.stream().filter(s->s.indexOf('.')>0).forEach(s->Assert.assertEquals(permissible2.hasPermission(s),true));
        parentTestPermissions.stream().filter(s->s.indexOf('.')<=0).forEach(s->Assert.assertEquals(permissible2.hasPermission(s),false));
    }

    private List<String> getRandomPermissions(){
        ArrayList<String> permission = new ArrayList<>();
        Random random = new Random(System.currentTimeMillis());
        for(int i = 0;i<100;i++)
            permission.add(new Double(random.nextDouble()).toString());

        return permission;
    }
}
