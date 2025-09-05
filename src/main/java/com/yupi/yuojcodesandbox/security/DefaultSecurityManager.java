package com.yupi.yuojcodesandbox.security;

import java.security.Permission;

public class DefaultSecurityManager extends SecurityManager {

    //检查所有权限
    public void checkPermission(Permission perm){
        System.out.println("默认不做任何权限限制");
        super.checkPermission(perm);
    }
}
