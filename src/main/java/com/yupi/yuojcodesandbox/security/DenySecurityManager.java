package com.yupi.yuojcodesandbox.security;

import java.security.Permission;

public class DenySecurityManager extends SecurityManager{
    public void checkPermission(Permission perm){
        throw new SecurityException("权限异常" + perm.toString());
    }
}
