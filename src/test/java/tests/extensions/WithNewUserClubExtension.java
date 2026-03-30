package tests.extensions;

import api.ApiClient;
import models.clubs.ClubModel;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import pages.ClubPage;

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace;

/** См. {@link WithNewUser}, {@link WithNewClub}, {@link ClubId}. */
public class WithNewUserClubExtension implements BeforeEachCallback, ParameterResolver {

    private static final Namespace NS = Namespace.create("bookclub.setup");
    private static final String KEY_CLUB_ID = "clubId";

    private final ApiClient api = new ApiClient();

    @Override
    public void beforeEach(ExtensionContext context) {
        context.getTestMethod().ifPresent(method -> {
            boolean user = method.isAnnotationPresent(WithNewUser.class);
            boolean club = method.isAnnotationPresent(WithNewClub.class);
            if (!user && !club) {
                return;
            }
            var login = new ClubPage().openBlankPageWithNewUser();
            if (club) {
                ClubModel created = api.clubs.createRandomClub(login.access());
                context.getStore(NS).put(KEY_CLUB_ID, created.id().toString());
            }
        });
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return parameterContext.getParameter().isAnnotationPresent(ClubId.class)
                && parameterContext.getParameter().getType() == String.class;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return extensionContext.getStore(NS).get(KEY_CLUB_ID, String.class);
    }
}
