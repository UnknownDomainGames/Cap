# Cap
[![](https://api.codeclimate.com/v1/badges/687ed1fae703c5786d17/maintainability)](https://codeclimate.com/github/UnknownDomainGame/Cap/maintainability)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/e882f018bf984b948fb1d77a26a42bcd)](https://www.codacy.com/manual/Mouse0w0/Cap?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=UnknownDomainGame/Cap&amp;utm_campaign=Badge_Grade)

Command &amp; permission library

## How to use

### Handler method:
```java
@Command("say")
public void say(@Sender CommandSender sender,String message){
    System.out.println(sender.getSenderName()+": "+message);
}
```

### Register

```java
SimpleCommandManager simpleCommandManager = new SimpleCommandManager();
MethodAnnotationCommand.getBuilder(simpleCommandManager)
        .addCommandHandler(this)
        .register();
```

### Execute

```java
simpleCommandManager.execute(testSender, "say hello!");
```