import requests
import json

def extract_korean_tags():
    try:
        url = "https://solved.ac/api/v3/tag/list"
        print("API 요청 중...")
        response = requests.get(url, headers={"User-Agent": "Mozilla/5.0"})
        print(f"응답 상태 코드: {response.status_code}")
        
        if response.status_code == 200:
            data = response.json()
            tags = []
            for item in data['items']:
                # displayNames 배열에서 language가 'ko'인 모든 항목의 name을 찾습니다
                korean_names = [name['name'] for name in item['displayNames'] if name['language'] == 'ko']
                tags.extend(korean_names)
            return tags
        else:
            print(f"API 요청 실패: {response.status_code}")
            return []
            
    except Exception as e:
        print(f"에러 발생: {str(e)}")
        return []

def convert_to_algorithm_requests(tags):
    return [{"name": tag} for tag in tags]

if __name__ == "__main__":
    print("태그 추출 시작...")
    korean_tags = extract_korean_tags()
    algorithm_requests = convert_to_algorithm_requests(korean_tags)
    
    # JSON 파일로 저장
    with open('algorithm_requests.json', 'w', encoding='utf-8') as f:
        json.dump(algorithm_requests, f, ensure_ascii=False, indent=2)
    
    print(f"\n총 {len(korean_tags)}개의 태그를 JSON 형식으로 변환했습니다.")
    print("결과가 'algorithm_requests.json' 파일로 저장되었습니다.")
