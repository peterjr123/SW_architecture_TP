from abc import *
from selenium import webdriver
from selenium.webdriver.common.by import By
import chromedriver_autoinstaller
import os
import time

class DocumentCrawler():
    def downloadDocument(self, documentList):
        filePathListPerSubject = {}
        if(self.validAccount(self.id, self.pw) == False):
            print("invalid account")
            return

        self.__login(self.id, self.pw)

        subjectNames = self.__getSubjectNameList()

        for subjectName in subjectNames:
            self.__accessSubjectPage(subjectName)
            filePathListPerSubject[subjectName] = self.downloadDocumentByCrawling(documentList)
            self.driver.get('https://ecampus.konkuk.ac.kr/ilos/main/member/login_form.acl')
        return filePathListPerSubject
    
    def installChromeDriver(self):
        chrome_ver = chromedriver_autoinstaller.get_chrome_version().split('.')[0]
        driver_path = f'./{chrome_ver}/chromedriver.exe'
        if os.path.exists(driver_path):
            print(f"chrom driver is insatlled: {driver_path}")
        else:
            print(f"install the chrome driver(ver: {chrome_ver})")
            chromedriver_autoinstaller.install(True)
        self.driver = webdriver.Chrome(driver_path)
        return

    def validAccount(self, id, pw):

        self.__login(id, pw)
        print(f"id: {id}, pw={pw}")
        return True

    def __getSubjectNameList(self):
        subjectNames = []
        subjects = self.driver.find_elements(By.CSS_SELECTOR, '.term_info ~ li')
        for subject in subjects:
            if(subject.get_attribute('class') != "term_info"):
                rawName = subject.find_element(By.CLASS_NAME, 'sub_open').text
                subjectNames.append(rawName[rawName.find(']')+1 : rawName.rfind('(')])
            else: 
                break
        return subjectNames

    def getDocumentList(self):
        documentList = {}
        subjectNames = []
        if(self.validAccount(self.id, self.pw) == False):
            print("invalid account")
            return

        self.__login(self.id, self.pw)

        subjectNames = self.__getSubjectNameList()

        for subjectName in subjectNames:
            self.__accessSubjectPage(subjectName)
            documentList[subjectName] = self.createDocumentListByCrawling()
            self.driver.get('https://ecampus.konkuk.ac.kr/ilos/main/member/login_form.acl')
        
        return documentList
    
    

    def __login(self, id, pw):
        self.driver.get('https://ecampus.konkuk.ac.kr/ilos/main/member/login_form.acl')
        self.driver.find_element(By.ID,'usr_id').send_keys(id)
        self.driver.find_element(By.ID,'usr_pwd').send_keys(pw)
        self.driver.find_element(By.ID, 'login_btn').click()

        while "수강과목" not in self.driver.page_source:
            time.sleep(1)
        return

    def __accessSubjectPage(self, subjectName):
        subjects = self.driver.find_elements(By.CLASS_NAME, 'sub_open')
        for subject in subjects:
            if(subject.text.find(subjectName) != -1):
                classNum = subject.get_attribute('kj')
                self.driver.execute_script('eclassRoom'+"('"+classNum+"')")
                break
        return  
    
    @abstractmethod
    def createDocumentListByCrawling():
        pass

    @abstractmethod
    def downloadDocumentByCrawling():
        pass

