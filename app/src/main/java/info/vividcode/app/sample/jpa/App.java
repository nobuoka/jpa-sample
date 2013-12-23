package info.vividcode.app.sample.jpa;

import java.util.List;
import java.util.HashMap;

import info.vividcode.app.sample.jpa.model.data.User;
import info.vividcode.app.sample.jpa.model.data.User_;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.eclipse.persistence.config.PersistenceUnitProperties;

/**
 * JPA を使用するサンプルコード。
 */
public class App {

    public static void main(String[] args) {
        // XXX サンプルコードが複雑になってしまうので例外処理を行なっていないが、
        // 本来は例外処理をすべき

        // Persistence unit と設定を指定して EntityManagerFactory を生成
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpa-sample", createDbSetting());
        EntityManager em = emf.createEntityManager();

        // トランザクションを開始して 3 つの User Entity を作成
        em.getTransaction().begin();
        createUser(em, "testuser1");
        createUser(em, "testuser2");
        createUser(em, "testuser3");
        em.getTransaction().commit();

        // User クラスで定義された NamedQuery を使って全ユーザーを取得して表示
        List<User> uu = (List<User>) em.createNamedQuery("User.findAll").getResultList();
        System.out.println("--- user list ---");
        for (User u : uu) {
            System.out.println(" - " + u.getName() + " (id: " + u.getId() + ")");
        }

        // Criteria API を使って "testuser1" という名前のユーザーの一覧を表示
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> r = cq.from(User.class);
        cq.select(r).where(cb.equal(r.get(User_.name), "testuser1"));
        List<User> uu2 = em.createQuery(cq).getResultList();
        System.out.println("--- user list (with name \"testuser1\") ---");
        for (User u : uu2) {
            System.out.println(" - " + u.getName() + " (id: " + u.getId() + ")");
        }

        em.close();
        emf.close();
    }

    private static void createUser(EntityManager em, String userName) {
        User user = new User(userName);
        em.persist(user);
    }

    private static HashMap<String,String> createDbSetting() {
        HashMap<String, String> settings = new HashMap<String, String>();
        // 接続先 DB の URL; Derby に接続し、データベースがなければ作成する
        settings.put(PersistenceUnitProperties.JDBC_URL, "jdbc:derby:jpa_sample.derby;create=true");
        // テーブルがない場合に作成する
        settings.put(PersistenceUnitProperties.DDL_GENERATION, "create-tables");
        return settings;
    }

}
