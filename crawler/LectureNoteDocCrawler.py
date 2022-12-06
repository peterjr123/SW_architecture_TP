import sys, os

sys.path.append(os.path.dirname(os.path.abspath(os.path.dirname(__file__))))

from crawler import DocumentCrawler as Crawler
from DocumentCrawler import DocumentCrawler
from abc import *
from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys
from selenium.webdriver import ActionChains
import time
import os


class LectureNoteDocCrawler(DocumentCrawler):
    def __init__(self, id, pw):
        self.id = id
        self.pw = pw
        self.driver = None
        self.driver_path = None
        # 절대경로이므로, 컴퓨터마다 수정해 주어야 함.
        self.downloadAbsolutePath = "C:/Users/joon/Downloads" 

    def downloadDocumentByCrawling(self, documentList):
        filePathList = []

        # 과목 페이지에서 강의자료 리스트 페이지 접속
        if(self.__accessToDocumentListPage() == False):
            return

        time.sleep(0.2)

        # 강의자료 리스트 페이지에서 각 강의자료 페이지에 접속후, 알맞은 문서 다운로드
        subPages = self.driver.find_elements(By.XPATH, '//td[@class="left"]')

        height = subPages[0].size['height']
        for subPage in subPages:
            self.driver.execute_script(f"window.scrollTo(0, {height});") # scrolling down을 안하면 클릭이 안되는 문제 발생
            height = height*2

            subPage.click()
            
            files = self.driver.find_elements(By.XPATH, '//a[@class="site-link"]')
            # time.sleep(0.2)
            for file in files:
                rawName = file.text
                fileName = rawName[rawName.find('-')+2 : rawName.rfind('(')-1]
                if(fileName in documentList):
                    file.click()
                    # sleep이 없으면 다운로드가 작동하지 않는 문제 발생
                    time.sleep(0.1) 
                    # !: 디렉토리내 동일한 이름이 존재해서 파일이 중복다운로드 되는 경우, 해당 파일이름에(1)이 붙게 됨.
                    # 따라서 이 부분에 대해서 적절한 처리가 필요할 수 있음.
                    filePathList.append(self.downloadAbsolutePath + "/" + fileName) 
            self.driver.back()
        
        return filePathList

    # documentListPage가 존재하지 않아서 접속하지 못한 경우에 false 반환
    def __accessToDocumentListPage(self):
        isDocumentListPageExist = False
        submains = self.driver.find_elements(By.CLASS_NAME, 'submain-notice')
        for i in range(len(submains)):
            submainName = submains[i].find_element(By.CLASS_NAME, 'title').text
            if(submainName.find("강의자료") != -1):
                submains[i].find_element(By.XPATH, './/div[1]').click()
                isDocumentListPageExist = True
                break
        return isDocumentListPageExist

    def createDocumentListByCrawling(self):
        documentList = []

        # 과목 페이지에서 강의자료 리스트 페이지 접속
        if(self.__accessToDocumentListPage() == False):
            return

        time.sleep(0.2)

        # 강의자료 리스트 페이지에서 각 강의자료 페이지에 접속후, 각 요소들을 가져옴.
        subPages = self.driver.find_elements(By.XPATH, '//td[@class="left"]')
        height = subPages[0].size['height']
        for subPage in subPages:
            self.driver.execute_script(f"window.scrollTo(0, {height});") # scrolling down을 안하면 클릭이 안되는 문제 발생
            height = height*2

            subPage.click()
            
            files = self.driver.find_elements(By.XPATH, '//a[@class="site-link"]')
            # time.sleep(0.2)
            for file in files:
                rawName = file.text
                fileName = rawName[rawName.find('-')+2 : rawName.rfind('(')-1]
                documentList.append(fileName)

            self.driver.back()
        
        return documentList

# id = input("id: ")
# pw = input("pw: ")
# crawler = LectureNoteDocCrawler(id, pw)
# crawler.installChromeDriver()
# crawler.validAccount()
# list = crawler.downloadDocument(["2202-소프트웨어아키텍처_실습자료12.pdf", "24. Review_of_Assignment#1.pdf", 
# "Ch.5 SYNCHRONOUS SEQUENTIAL LOGIC - PART A.pdf", "Ch.5 SYNCHRONOUS SEQUENTIAL LOGIC - PART C.pdf", "Ch.5 SYNCHRONOUS SEQUENTIAL LOGIC - PART B.pdf"])


# for key in list:
#     print("강의 이름:", key)
#     if(list[key] is None):
#         continue
#     for item in list[key]:
#         print(item)