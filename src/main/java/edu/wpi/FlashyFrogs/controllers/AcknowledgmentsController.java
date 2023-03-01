package edu.wpi.FlashyFrogs.controllers;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;

public class AcknowledgmentsController {
  @FXML private Hyperlink OSS;
  @FXML private Hyperlink MassIcon1;
  @FXML private Hyperlink MassIcon2;
  @FXML private Hyperlink HospitalPic;
  @FXML private Hyperlink openjfx;
  @FXML private Hyperlink lombok;
  @FXML private Hyperlink Spotless;
  @FXML private Hyperlink Jacoco;
  @FXML private Hyperlink JitPack;
  @FXML private Hyperlink IntelliJ;
  @FXML private Hyperlink SceneBuilder;
  @FXML private Hyperlink Figma;
  @FXML private Hyperlink drawio;
  @FXML private Hyperlink Slack;
  @FXML private Hyperlink Discord;
  @FXML private Hyperlink Docker;
  @FXML private Hyperlink GitHub;
  @FXML private Hyperlink Ableton;
  @FXML private Hyperlink Audacity;
  @FXML private Hyperlink Icons;
  @FXML private Hyperlink JAXBAPI;
  @FXML private Hyperlink OJDBC10;
  @FXML private Hyperlink OJDBC8;
  @FXML private Hyperlink SLF4SimpleBinding;
  @FXML private Hyperlink OracleDatabaseJDBCUCP;
  @FXML private Hyperlink Hibernate;
  @FXML private Hyperlink Ehcache;
  @FXML private Hyperlink JAXBCore;
  @FXML private Hyperlink JAXBRuntime;
  @FXML private Hyperlink PostgreSQL;
  @FXML private Hyperlink Argon2;
  @FXML private Hyperlink FlexBox;
  @FXML private Hyperlink fxsvg;
  @FXML private Hyperlink JavaNativeAccess;
  @FXML private Hyperlink HibernateORM;
  @FXML private Hyperlink JFoeniX;
  @FXML private Hyperlink ApacheCommons;
  @FXML private Hyperlink TestFXCore;
  @FXML private Hyperlink JUnitJupiterAPI;
  @FXML private Hyperlink TestFXJUnit;
  @FXML private Hyperlink JUnitJupiterEngine;
  @FXML private Hyperlink JUnitJupiterAggregator;
  @FXML private Hyperlink SLF4API;
  @FXML private Hyperlink ApacheDerby;
  @FXML private Hyperlink MaterialFX;
  @FXML private Hyperlink GestureFX;
  @FXML private Hyperlink SQLiteJDBC;
  @FXML private Hyperlink ControlsFX;
  @FXML private Hyperlink JavaFX19SDK;
  @FXML private Hyperlink JavaOpenJDK;

  @FXML
  public void initialize() {
    setLinkAction(JavaOpenJDK, "https://learn.microsoft.com/en-us/java/openjdk/download");
    setLinkAction(JavaFX19SDK, "https://gluonhq.com/products/javafx/");
    setLinkAction(
        MaterialFX, "https://mvnrepository.com/artifact/io.github.palexdev/materialfx/11.13.8");
    setLinkAction(GestureFX, "https://github.com/tom91136/GestureFX");
    setLinkAction(
        ControlsFX, "https://mvnrepository.com/artifact/org.controlsfx/controlsfx/11.1.2");
    setLinkAction(SQLiteJDBC, "https://mvnrepository.com/artifact/org.xerial/sqlite-jdbc/3.30.1");
    setLinkAction(ApacheDerby, "https://db.apache.org/derby/");
    setLinkAction(SLF4API, "https://mvnrepository.com/artifact/org.slf4j/slf4j-api/1.7.30");
    setLinkAction(
        SLF4SimpleBinding, "https://mvnrepository.com/artifact/org.slf4j/slf4j-simple/1.7.30");
    setLinkAction(
        OracleDatabaseJDBCUCP, "https://mvnrepository.com/artifact/com.oracle.database.jdbc/ucp");
    setLinkAction(
        OJDBC8, "https://mvnrepository.com/artifact/com.oracle.database.jdbc/ojdbc8/19.6.0.0");
    setLinkAction(Hibernate, "https://hibernate.org/orm/releases/6.1/");
    setLinkAction(Ehcache, "https://mvnrepository.com/artifact/org.ehcache/ehcache/3.10.6");
    setLinkAction(JAXBCore, "https://mvnrepository.com/artifact/com.sun.xml.bind/jaxb-core/4.0.2");
    setLinkAction(JAXBAPI, "https://mvnrepository.com/artifact/javax.xml.bind/jaxb-api/2.3.1");
    setLinkAction(
        JAXBRuntime, "https://mvnrepository.com/artifact/org.glassfish.jaxb/jaxb-runtime/4.0.2");
    setLinkAction(
        PostgreSQL, "https://mvnrepository.com/artifact/org.postgresql/postgresql/42.5.1");
    setLinkAction(Argon2, "https://mvnrepository.com/artifact/de.mkammerer/argon2-jvm/2.11");
    setLinkAction(FlexBox, "https://github.com/onexip/FlexBoxFX/releases");
    setLinkAction(fxsvg, "https://github.com/hervegirod/fxsvgimage");
    setLinkAction(
        JavaNativeAccess, "https://mvnrepository.com/artifact/net.java.dev.jna/jna/5.12.1");
    setLinkAction(
        HibernateORM,
        "https://mvnrepository.com/artifact/org.hibernate.orm/hibernate-c3p0/6.1.6.Final");
    setLinkAction(JFoeniX, "https://mvnrepository.com/artifact/com.jfoenix/jfoenix/9.0.10");
    setLinkAction(
        ApacheCommons, "https://mvnrepository.com/artifact/org.apache.commons/commons-math3/3.6.1");
    setLinkAction(
        TestFXCore, "https://mvnrepository.com/artifact/org.testfx/testfx-core/4.0.16-alpha");
    setLinkAction(
        JUnitJupiterAPI,
        "https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter-api/5.8.1");
    setLinkAction(
        TestFXJUnit, "https://mvnrepository.com/artifact/org.testfx/testfx-junit5/4.0.16-alpha");
    setLinkAction(OJDBC10, "https://mvnrepository.com/artifact/com.oracle.ojdbc/ojdbc10/19.3.0.0");
    setLinkAction(
        JUnitJupiterEngine,
        "https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter-engine/5.8.1");
    setLinkAction(
        JUnitJupiterAggregator,
        "https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter/5.6.0");
    setLinkAction(Icons, "https://www.svgrepo.com/");
    setLinkAction(
        MassIcon1,
        "https://www.massgeneralbrigham.org/content/experience-fragments/mgb-global/global/en/site/footer/master/_jcr_content/root/container_1627538517/container/container/image.coreimg.svg/1672848798937/logo-with-white-text.svg");
    setLinkAction(
        MassIcon2,
        "https://give.brighamandwomens.org/wp-content/uploads/2021/06/Brigham_and_Womens_Hospital_horiz_rgb-1.png");
    setLinkAction(
        HospitalPic,
        "https://www.massgeneralbrigham.org/content/mgb-global/global/en/patient-care/international/about/brigham-and-womens-hospital/_jcr_content/root/container_924996778/hero_banner.coreimg.jpeg/1667229588893/international-bwh-1428x1110.jpeg");
    setLinkAction(
        Jacoco,
        "https://www.jacoco.org/jacoco/trunk/index.html#:~:text=JaCoCo%20is%20a%20free%20Java,25%20based%20on%20commit%20a68effb42f89682c275cc1e26418793191512985.");
    setLinkAction(Spotless, "https://github.com/diffplug/spotless");
    setLinkAction(lombok, "https://plugins.gradle.org/plugin/io.freefair.lombok");
    setLinkAction(openjfx, "https://plugins.gradle.org/plugin/org.openjfx.javafxplugin");
    setLinkAction(JitPack, "https://jitpack.io/");
    setLinkAction(OSS, "http://oss.sonatype.org/content/repositories/snapshots//");
    setLinkAction(IntelliJ, "https://www.jetbrains.com/idea/");
    setLinkAction(SceneBuilder, "https://gluonhq.com/products/scene-builder/");
    setLinkAction(Figma, "https://www.figma.com/");
    setLinkAction(drawio, "http://draw.io");
    setLinkAction(Slack, "https://slack.com/");
    setLinkAction(Discord, "https://discordapp.com/");
    setLinkAction(Docker, "https://www.docker.com/");
    setLinkAction(GitHub, "https://github.com/");
    setLinkAction(Ableton, "https://www.ableton.com/en/live/");
    setLinkAction(Audacity, "https://www.audacityteam.org/");
  }

  public void setLinkAction(Hyperlink link, String url) {
    link.setOnAction(
        t -> {
          try {
            java.awt.Desktop.getDesktop().browse(new URI(url));
          } catch (IOException e) {
            e.printStackTrace();
          } catch (URISyntaxException e) {
            e.printStackTrace();
          }
        });
  }
}
