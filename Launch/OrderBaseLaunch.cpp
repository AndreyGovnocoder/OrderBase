#include "OrderBaseLaunch.h"

int OrderBaseLaunch::_primaryInDB;
int OrderBaseLaunch::_secondaryInDB;
QString OrderBaseLaunch::_pathToJAR;
QString OrderBaseLaunch::_pathToDB;
const QString OrderBaseLaunch::DB_NAME = "OrderBasePrint_dataBase.db";
const QString OrderBaseLaunch::JAR_NAME = "OrderBase.jar";

OrderBaseLaunch::OrderBaseLaunch(QWidget *parent)
    : QMainWindow(parent)
{
    ui.setupUi(this);
    if (QFile(QDir::currentPath() + "/icons/error.png").exists())
        ui.iconLabel->setPixmap(QPixmap("./icons/error.png"));
}


void OrderBaseLaunch::setPathToDBSlot()
{
    QString pathToDB = QFileDialog::getExistingDirectory(this,
        "Указать папку с базами данных",
        QDir::currentPath());

    if (pathToDB.isEmpty())
        return;
    setPathToDB(pathToDB);
}

void OrderBaseLaunch::setPathToUpdateFileSlot()
{
    QString pathToUpdateFile = QFileDialog::getExistingDirectory(this,
        "Указать папку с файлом обновления",
        QDir::currentPath());
    if (pathToUpdateFile.isEmpty())
        return;
    setPathToUpdateFile(pathToUpdateFile);
}

bool OrderBaseLaunch::setVersionsFromDB(const QString& fullPath)
{
    auto db = QSqlDatabase::addDatabase("QSQLITE");
    db.setDatabaseName(fullPath);
    const bool ok = db.open();
    if (!ok)
    {
        QString error = db.lastError().text();
        QMessageBox::critical(this, "Ошибка", "Ошибка подключения к базе данных: " + error);
        return ok;
    }

    QSqlQuery query(QSqlDatabase::database(fullPath));
    if (query.exec("SELECT * FROM versions"))
    {
        query.first();
        
        OrderBaseLaunch::_primaryInDB = query.value(1).toInt();
        OrderBaseLaunch::_secondaryInDB = query.value(2).toInt();
        return ok;
    }
    return false;
}

void OrderBaseLaunch::getIcoFromDB(const QString& fullPath)
{
    auto db = QSqlDatabase::addDatabase("QSQLITE");
    db.setDatabaseName(fullPath);
    const bool ok = db.open();
    if (!ok)
    {
        QString error = db.lastError().text();
        QMessageBox::critical(this, "Ошибка", "Ошибка подключения к базе данных: " + error);
        return;
    }

    QSqlQuery query(QSqlDatabase::database(fullPath));
    if (query.exec("SELECT image FROM images WHERE _id = 12"))
    {
        query.first();

        QByteArray image = query.value(1).toByteArray();
    }
}

void OrderBaseLaunch::launchJavaApp()
{
    if (QFile::exists("OrderBase.jar"))
    {
        ShellExecute(nullptr, L"open", L"JRE\\bin\\javaw.exe", L" -jar OrderBase.jar", nullptr, SW_RESTORE);
    }
    else if (QFile::copy(_pathToJAR, "OrderBase.jar"))
    {
        ShellExecute(nullptr, L"open", L"JRE\\bin\\javaw.exe", L" -jar OrderBase.jar", nullptr, SW_RESTORE);
    }
    else
        QMessageBox::warning(this, "Ошибка", "Что-то пошло не так :(");
}

void OrderBaseLaunch::setPathToDB(const QString& pathToDB)
{
    if (pathToDB.isEmpty())
        return;

    const QString fullPath = pathToDB + "\\" + DB_NAME;
    if (!setVersionsFromDB(fullPath))
    {
        QMessageBox::critical(this, "Ошибка", "Ошибка подключения к базе данных\nВозможно указан неверный путь к базам данных:\n" + pathToDB);
        return;
    }

    QSettings settings(QDir::currentPath() + "/src/settings.ini", QSettings::IniFormat);
    settings.setIniCodec("UTF-8");
    settings.setValue("PathToDB", pathToDB);
    _pathToDB = pathToDB;
    if (!_pathToJAR.isEmpty())
        updateJAR(settings);
}

void OrderBaseLaunch::setPathToUpdateFile(const QString& pathToUpdateFile)
{
    if (pathToUpdateFile.isEmpty())
        return;

    _pathToJAR = pathToUpdateFile + "\\" + JAR_NAME;
    if (!QFile::exists(_pathToJAR))
    {
        QMessageBox::critical(this, "Ошибка", "Возможно указан неверный путь к файлу обновления:\n" + pathToUpdateFile);
        return;
    }
    
    QSettings settings(QDir::currentPath() + "/src/settings.ini", QSettings::IniFormat);
    settings.setIniCodec("UTF-8");
    settings.setValue("PathToUpdateFile", pathToUpdateFile);
    if (!_pathToDB.isEmpty())
    {
        const QString fullPath = _pathToDB + "\\" + DB_NAME;
        if (!setVersionsFromDB(fullPath))
        {
            QMessageBox::critical(this, "Ошибка", "Ошибка подключения к базе данных\nВозможно указан неверный путь к базам данных:\n" + _pathToDB);
            return;
        }
        updateJAR(settings);
    }
}

void OrderBaseLaunch::updateJAR(QSettings& settings)
{
    QMessageBox::warning(this, "Обновление", "Oбновление до " + QString::number(OrderBaseLaunch::_primaryInDB) + "." + QString::number(OrderBaseLaunch::_secondaryInDB));

    if (QFile::exists("OrderBase.jar"))
        QFile::remove("OrderBase.jar");

    if (QFile::copy(_pathToJAR, "OrderBase.jar"))
    {
        this->close();
        settings.setValue("Version", QString::number(OrderBaseLaunch::_primaryInDB) + "." + QString::number(OrderBaseLaunch::_secondaryInDB));
        launchJavaApp();
    }
    else
        QMessageBox::warning(this, "Ошибка", "Что-то пошло не так при обновлении файла :(");
}

void OrderBaseLaunch::setText(const QString& text)
{
    ui.textLabel->setText(text);
}

void OrderBaseLaunch::setEnabledPathToDBbtn(bool enabled)
{
    ui.setPathToDBbtn->setEnabled(enabled);
}

void OrderBaseLaunch::setEnabledPathToUdateFileBtn(bool enabled)
{
    ui.setPathToUdateFileBtn->setEnabled(enabled);
}
