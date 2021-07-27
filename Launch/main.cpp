#include "OrderBaseLaunch.h"
#include <QtWidgets/QApplication>

int main(int argc, char *argv[])
{
    QApplication a(argc, argv);
    if (QFile(QDir::currentPath() + "/icons/OrderBaseLogo.png").exists())
        a.setWindowIcon(QIcon("./icons/OrderBaseLogo.png"));
    OrderBaseLaunch app;

    QSettings settings(QDir::currentPath() + "/src/settings.ini", QSettings::IniFormat);
    settings.setIniCodec("UTF-8");
    
    QString pathToDB;
    QString fullPathToDB;
    QString pathToUpdateFile;
    QString fullPathToUpdateFile;

    int primary;
    int secondary;
    bool dbExists = false;
    bool updateFileExists = false;

    if (!QDir("src").exists())
        QDir().mkdir("src");

    if (QFile(QDir::currentPath() + "/src/settings.ini").exists())
    {
        pathToDB = settings.value("PathToDB").toString();
        pathToUpdateFile = settings.value("PathToUpdateFile").toString();
        primary = settings.value("Version").toString().split('.')[0].toInt();
        secondary = settings.value("Version").toString().split('.')[1].toInt();

        fullPathToDB = pathToDB + "/" + app.DB_NAME;
        fullPathToUpdateFile = pathToUpdateFile + "/" + app.JAR_NAME;

        if (QFile(fullPathToUpdateFile).exists() && !pathToUpdateFile.isEmpty())
        {
            updateFileExists = true;
            app._pathToJAR = fullPathToUpdateFile;
        }

        if (QFile(fullPathToDB).exists() && !pathToDB.isEmpty())
        {
            dbExists = true;
            app._pathToDB = pathToDB;
        }
    }

    if (updateFileExists && dbExists)
    {
        if (app.setVersionsFromDB(fullPathToDB))
        {
            if (primary < app._primaryInDB || secondary < app._secondaryInDB)
            {
                app.updateJAR(settings);
                return 0;
            }
            else
            {
                app.getIcoFromDB(fullPathToDB);
                app.launchJavaApp();
                return 0;
            }
        }
    }

    if (!updateFileExists && !dbExists)
        app.setText("Отсутствует доступ к базе данных и к файлу обновления");
    else if (!updateFileExists)
    {
        app.setText("Отсутствует доступ к файлу обновления");
        app.setEnabledPathToDBbtn(false);
    }
    else if (!dbExists)
    {
        app.setText("Отсутствует доступ к базе данных");
        app.setEnabledPathToUdateFileBtn(false);
    }

    app.show();
    return a.exec();
}

