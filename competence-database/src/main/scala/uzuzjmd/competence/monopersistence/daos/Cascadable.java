package uzuzjmd.competence.monopersistence.daos;

/**
 * Created by dehne on 11.01.2016.
 */
public interface Cascadable {
    void persistMore() throws Exception;
}