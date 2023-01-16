for i in range(0,50):
  df=open(str(i) + '.in', 'w')
  df.write(str(i) + "\n")
  df.write(str(i)+ "\n")
  for j in range(1,i+1):
    df.write(str(j)+ "\n")
  df.close()



  hyperfine --warmup 100  --min-runs 500  './a.out < ./hyperfine/1.in' './a.out < ./hyperfine/2.in' './a.out < ./hyperfine/3.in' './a.out < ./hyperfine/4.in' './a.out < ./hyperfine/5.in' './a.out < ./hyperfine/6.in' './a.out < ./hyperfine/7.in' './a.out < ./hyperfine/8.in' './a.out < ./hyperfine/9.in' './a.out < ./hyperfine/10.in' './a.out < ./hyperfine/11.in' './a.out < ./hyperfine/12.in' './a.out < ./hyperfine/13.in' './a.out < ./hyperfine/14.in' './a.out < ./hyperfine/15.in' './a.out < ./hyperfine/16.in' './a.out < ./hyperfine/17.in' './a.out < ./hyperfine/18.in' './a.out < ./hyperfine/19.in' './a.out < ./hyperfine/20.in' './a.out < ./hyperfine/21.in' './a.out < ./hyperfine/22.in' './a.out < ./hyperfine/23.in' './a.out < ./hyperfine/24.in' './a.out < ./hyperfine/25.in' './a.out < ./hyperfine/26.in' './a.out < ./hyperfine/27.in' './a.out < ./hyperfine/28.in' './a.out < ./hyperfine/29.in' './a.out < ./hyperfine/30.in' './a.out < ./hyperfine/31.in' './a.out < ./hyperfine/32.in' './a.out < ./hyperfine/33.in' './a.out < ./hyperfine/34.in' './a.out < ./hyperfine/35.in' './a.out < ./hyperfine/36.in' './a.out < ./hyperfine/37.in' './a.out < ./hyperfine/38.in' './a.out < ./hyperfine/39.in' './a.out < ./hyperfine/40.in' './a.out < ./hyperfine/41.in' './a.out < ./hyperfine/42.in' './a.out < ./hyperfine/43.in' './a.out < ./hyperfine/44.in' './a.out < ./hyperfine/45.in' './a.out < ./hyperfine/46.in' './a.out < ./hyperfine/47.in' './a.out < ./hyperfine/48.in' './a.out < ./hyperfine/49.in'
