package com.pulse.coloredcauldron;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.jetbrains.annotations.NotNull;

public class PulseCoreLoader implements PluginLoader {
    @Override
    public void classloader(@NotNull PluginClasspathBuilder classpathBuilder) {
        MavenLibraryResolver resolver = new MavenLibraryResolver();

        // kotlin
        resolver.addDependency(new Dependency(new DefaultArtifact("org.jetbrains.kotlin:kotlin-stdlib:2.3.0"), null));

        // commands
        resolver.addDependency(new Dependency(new DefaultArtifact("dev.jorel:commandapi-paper-shade:11.1.0"), null));
        resolver.addDependency(new Dependency(new DefaultArtifact("dev.jorel:commandapi-kotlin-paper:11.1.0"), null));
        
        // utils
        resolver.addDependency(new Dependency(new DefaultArtifact("com.jeff-media:MorePersistentDataTypes:2.4.0"), null));
        resolver.addDependency(new Dependency(new DefaultArtifact("com.jeff-media:custom-block-data:2.2.4"), null));

        // repos
        resolver.addRepository(new RemoteRepository.Builder("paper", "default", "https://repo.papermc.io/repository/maven-public/").build());

        classpathBuilder.addLibrary(resolver);
    }
}
