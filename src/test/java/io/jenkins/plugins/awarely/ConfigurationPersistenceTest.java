package io.jenkins.plugins.awarely;

import static org.junit.jupiter.api.Assertions.*;

import hudson.XmlFile;
import hudson.model.Saveable;
import hudson.model.listeners.SaveableListener;
import java.util.concurrent.atomic.AtomicInteger;
import org.htmlunit.html.HtmlCheckBoxInput;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.TestExtension;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

@WithJenkins
class ConfigurationPersistenceTest {
    @Test
    void directSettersPersistWithoutAnExplicitSave(JenkinsRule j) {
        var config = AwarelyConfiguration.get();
        var writes = j.jenkins.getExtensionList(ConfigurationWrites.class).getFirst();
        config.setAllowToolDownloads(true);
        assertTrue(new AwarelyConfiguration().isAllowToolDownloads());
        config.setRemoteJobs("folder/check");
        assertEquals("folder/check", new AwarelyConfiguration().getRemoteJobs());
        config.setHostScanJobs("folder/host");
        assertEquals("folder/host", new AwarelyConfiguration().getHostScanJobs());
        config.setSyncJobs("folder/deploy");
        assertEquals("folder/deploy", new AwarelyConfiguration().getSyncJobs());
        config.setTrustedAgents("agent-one\nagent-two");
        assertEquals("agent-one\nagent-two", new AwarelyConfiguration().getTrustedAgents());
        config.setApiOrigins("https://monitor.example.invalid");
        assertEquals("https://monitor.example.invalid", new AwarelyConfiguration().getApiOrigins());
        assertEquals(6, writes.count.get());

        config.setAllowToolDownloads(false);
        assertFalse(new AwarelyConfiguration().isAllowToolDownloads());
        config.setRemoteJobs(null);
        assertEquals("", new AwarelyConfiguration().getRemoteJobs());
        assertEquals(8, writes.count.get());
    }

    @Test
    void submittingMultipleFieldsWritesOnceAndSurvivesReload(JenkinsRule j) throws Exception {
        var writes = j.jenkins.getExtensionList(ConfigurationWrites.class).getFirst();
        var page = j.createWebClient().goTo("configure");
        var form = page.getFormByName("config");
        ((HtmlCheckBoxInput) form.getInputByName("_.allowToolDownloads")).setChecked(true);
        form.getTextAreaByName("_.remoteJobs").setText("folder/check");
        form.getTextAreaByName("_.hostScanJobs").setText("folder/host");
        form.getTextAreaByName("_.syncJobs").setText("folder/deploy");
        form.getTextAreaByName("_.trustedAgents").setText("trusted-linux");
        form.getTextAreaByName("_.apiOrigins").setText("https://monitor.example.invalid");
        assertEquals(0, writes.count.get());
        j.submit(form);
        assertEquals(1, writes.count.get());
        var loaded = new AwarelyConfiguration();
        assertTrue(loaded.isAllowToolDownloads());
        assertEquals("folder/check", loaded.getRemoteJobs());
        assertEquals("folder/host", loaded.getHostScanJobs());
        assertEquals("folder/deploy", loaded.getSyncJobs());
        assertEquals("trusted-linux", loaded.getTrustedAgents());
        assertEquals("https://monitor.example.invalid", loaded.getApiOrigins());
    }

    @Test
    void invalidSetterValueDoesNotChangeMemoryOrDisk(JenkinsRule j) {
        var config = AwarelyConfiguration.get();
        var writes = j.jenkins.getExtensionList(ConfigurationWrites.class).getFirst();
        config.setRemoteJobs("approved");
        assertThrows(IllegalArgumentException.class, () -> config.setRemoteJobs("invalid\0job"));
        assertThrows(IllegalArgumentException.class, () -> config.setRemoteJobs("x".repeat(32001)));
        assertEquals("approved", config.getRemoteJobs());
        assertEquals("approved", new AwarelyConfiguration().getRemoteJobs());
        assertEquals(1, writes.count.get());
    }

    @TestExtension
    public static final class ConfigurationWrites extends SaveableListener {
        final AtomicInteger count = new AtomicInteger();

        @Override
        public void onChange(Saveable object, XmlFile file) {
            if (object instanceof AwarelyConfiguration) count.incrementAndGet();
        }
    }
}
