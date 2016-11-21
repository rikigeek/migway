package migway.core.config.rules;

public interface ICompletionRule {
    public boolean validate(Object outputPojo, String[] args);
}
