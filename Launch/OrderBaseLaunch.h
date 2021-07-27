#pragma once

#include <QtWidgets/QMainWindow>
#include "ui_OrderBaseLaunch.h"
#include <QSettings>
#include <qfile.h>
#include <qfiledialog.h>
#include <QSqlDatabase>
#include <QtSql>
#include <QtWidgets/qmessagebox.h>
#include <shlwapi.h>

class OrderBaseLaunch : public QMainWindow
{
    Q_OBJECT

public:
    OrderBaseLaunch(QWidget *parent = Q_NULLPTR);
    bool setVersionsFromDB(const QString& fullPath);
    void getIcoFromDB(const QString& fullPath);
    static const QString DB_NAME;
    static const QString JAR_NAME;
    static QString _pathToJAR;
    static QString _pathToDB;
    static int _primaryInDB;
    static int _secondaryInDB;
    void launchJavaApp();
    void setPathToDB(const QString& pathToDB);
    void setPathToUpdateFile(const QString& pathToUpdateFile);
    void updateJAR(QSettings& settings);
    void setText(const QString& text);
    void setEnabledPathToDBbtn(bool enabled);
    void setEnabledPathToUdateFileBtn(bool enabled);

private:
    Ui::OrderBaseLaunchClass ui;

private slots:
    void setPathToDBSlot();
    void setPathToUpdateFileSlot();
};
