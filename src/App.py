import csv
import random
import datetime

# Same course map as in courses.csv
prefixes = []
prefixes += [f"EB{i}" for i in range(1,16)]
prefixes += [f"CB{i}" for i in range(1,17)]
prefixes += [f"ENG{i}" for i in range(1,11)]
prefixes += [f"BC{i}" for i in range(1,11)]

courses = [
    "Computer Science","Applied Computer Science","Information Technology","Software Engineering",
    "Electrical Engineering","Mechanical Engineering","Civil Engineering","Chemical Engineering",
    "Biomedical Engineering","Actuarial Science","Mathematics","Statistics","Physics","Chemistry",
    "Biology","Environmental Science","Agricultural Science","Economics","Finance","Accounting",
    "Business Administration","Marketing","Human Resource Management","Political Science","Sociology",
    "Psychology","Philosophy","History","Literature","Education","Nursing","Medicine","Pharmacy",
    "Law","Criminology","Anthropology","Geography","Architecture","Fine Arts","Music","Theatre Arts",
    "Data Science","Artificial Intelligence","Cybersecurity","Cloud Computing","Robotics","Astronomy",
    "Biotechnology","Food Science","Veterinary Medicine","Public Health","Social Work","International Relations"
]

course_map = {}
for i, cname in enumerate(courses):
    prefix = prefixes[i % len(prefixes)]
    course_map[prefix] = cname

# Names and counties
first_names = ["John","Mary","Peter","Jane","Kelvin","Alice","Brian","Samuel","Grace","David",
    "Lucy","George","Sophia","Michael","Esther","Josephine","Daniel","Mercy","Paul","Caroline",
    "James","Rose","Anthony","Beatrice","Charles","Florence","Patrick","Catherine","Stephen","Eunice",
    "Victor","Janet","Martin","Hannah","Elijah","Sarah","Dennis","Annette","Collins","Rebecca",
    "Francis","Naomi","Alex","Diana","Eric","Joyce","Kennedy","Ruth","Allan","Lydia"]

last_names = ["Mutinda","Wanjiru","Atieno","Mwangi","Njeri","Otieno","Kiptoo","Achieng","Kamau","Wairimu",
    "Oloo","Naliaka","Kariuki","Chebet","Moraa","Omondi","Wanjiku","Kiplangat","Cheruiyot","Ochieng",
    "Nyambura","Kiprono","Anyango","Maina","Mutua","Akinyi","Mwenda","Wambui","Chepkorir","Njoroge",
    "Kipkemboi","Oduor","Cherono","Barasa","Were","Okoth","Makori","Nyakundi","Bosire","Simiyu",
    "Koech","Langat","Ruto","Sang","Kosgey","Maritim","Kiplagat","Chepkorir","Kibet","Kipchirchir"]

counties = ["Nairobi","Mombasa","Kisumu","Nakuru","Kiambu","Meru","Eldoret"]

# Distribution logic
total_students = 10000
course_distribution = {}
priority_courses = ["Education","Computer Science","Nursing","Electrical Engineering","Mechanical Engineering","Civil Engineering","Chemical Engineering"]

for cname in courses:
    course_distribution[cname] = 20  # baseline per course

remaining = total_students - (len(courses) * 20)

for cname in priority_courses:
    course_distribution[cname] += 500
    remaining -= 500

while remaining > 0:
    cname = random.choice(courses)
    course_distribution[cname] += 1
    remaining -= 1

# Helper to calculate year_of_study
def calculate_year_of_study(suffix):
    admission_year = 2000 + int(suffix)   # "23" -> 2023
    now = datetime.date.today()
    current_year = now.year
    diff = current_year - admission_year
    if now.month >= 8:  # August or later
        diff += 1
    return max(diff, 1)

# Generate learners
suffixes = ["22", "23", "24", "25"]

with open("learners.csv","w",newline="") as f:
    writer = csv.writer(f)
    writer.writerow(["name","password","email","idNo","address","regNo","phoneNo","courseID","courseName","County","year_of_study"])
    
    student_index = 0
    for cid, cname in course_map.items():
        count = course_distribution.get(cname, 0)
        for j in range(count):
            name = f"{random.choice(first_names)} {random.choice(last_names)}"
            password = f"PW{student_index+1000}"
            email = f"{name.lower().replace(' ','')}{student_index}@gmail.com"
            idNo = random.randint(20000000,49999999)
            address = random.randint(100,999)
            
            # Cycle regNo suffix among 22, 23, 24, 25
            suffix = suffixes[student_index % len(suffixes)]
            regNo = f"{cid}/{60000+student_index}/{suffix}"
            
            # Calculate year_of_study
            year_of_study = calculate_year_of_study(suffix)
            
            phoneNo = f"07{random.randint(10000000,99999999)}"
            county = random.choice(counties)
            
            writer.writerow([name,password,email,idNo,address,regNo,phoneNo,cid,cname,county,year_of_study])
            student_index += 1

# Summary
print("Course distribution summary:")
for cname, count in course_distribution.items():
    print(f"{cname}: {count} students")
