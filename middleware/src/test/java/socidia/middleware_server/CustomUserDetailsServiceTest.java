package socidia.middleware_server;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.MockitoRule;
import socidia.middleware_server.model.User;
import socidia.middleware_server.repository.UserRepository;
import socidia.middleware_server.service.CustomUserDetailsService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class CustomUserDetailsServiceTest {
    @Mock
    private UserRepository mockedUserRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testLoadUserByUserName() {

    }
}
