
@MetaInfServices
class ClassIml implements ClassInterface{

}


public static ClassInterface loadService() {
        ServiceLoader<ClassInterface> loggerServiceLoader = ServiceLoader.load(ClassInterface.class);
        ClassInterface result;
        Iterator<ClassInterface> iterator = loggerServiceLoader.iterator();
        if (iterator.hasNext()) {
            result = iterator.next();
        } else {
            throw new RuntimeException(
                    taskClass.getSimpleName() + " implementation not found, check if your task solution implementation implements task interface and contains @MetaInfServices annotation");
        }
        return result;
}
