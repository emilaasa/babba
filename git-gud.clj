(require '[clojure.java.shell :refer [sh]])

(defn git-init! []
  (sh "git" "init"))

(defn add [file-name]
  (sh "git" "add" file-name))

(defn commit [msg]
  (sh "git" "commit" "-m" msg))

(defn create-branch [branch]
  (sh "git" "checkout" "-b" (name branch)))

(defn checkout [branch]
  (sh "git" "checkout" (name branch)))

(defn gofmt [file-name]
  (sh "go" "fmt" file-name))

(defn append-to-file [file-name s]
  (spit file-name s :append true))

(def package "package main")

(defn goodfunc [func-name] (format "\n\nfunc %s(){
    if  1 == 0 {
       fmt.Println(\"Hello \")
            }}" func-name))

(defn okfunc [func-name] (format "\n\nfunc %s(){
                                  if 1 == 0 {
                                  if 1 == 0 {
                                   fmt.Println(\"print\")
                                  }
                                  }
                                  }" func-name))
(defn badfunc [func-name] (format "\n\nfunc %s(){
                                  if 1 == 0 {
                                  if 1 == 0 {
                                  if 1 == 0 {
                                  if 1 == 0 {
                                  if 1 == 0 {
                                  if 1 == 0 {
                                  if 1 == 0 {
                                  if 1 == 0 {
                                  if 1 == 0 {
                                  if 1 == 0 {
                                  if 1 == 0 {
                                  if 1 == 0 {
                                  if 1 == 0 {
                                  if 1 == 0 {
                                  }
                                  }
                                  }
                                  }
                                  }
                                  }
                                  }
                                  }
                                  }
                                  }
                                  }
                                  }
                                  }
                                  }
                                  }" func-name))
(defn delete-git-repo! []
  (sh "rm" "-r" ".git/"))

;; begin repo
(spit "README.md" "# README")
(add "README.md")
(commit "Initial commit on master - the README")


(comment
  ; set up the repo
  (git-init!)
  (spit "README.md" "# README")
  (add "README.md")
  (commit "Initial commit on master - the README")
  ; create a first commit
  (def file-name "main.go")
  (spit file-name (str package (goodfunc "main")))
  (gofmt file-name)
  (add file-name)
  (commit (str "A simple hello world: " file-name))

  (create-branch :develop)
  ; create a develop branch and simulate some work on it, so it's suitably ahead of master
  ; and accumulate some complexity on it
  ; this will make the delta between the coming feature branch and develop smaller
  (doseq [i (range 1 3)]
    (append-to-file "main.go" (okfunc (str "ok" i)))
    (gofmt "main.go")
    (add "main.go")
    (commit (format "%d - an ok commit to develop - adding a func" i)))

  (checkout :master)
  ; add reasonable commits to master branch
  (doseq [i (range 1 2)]
    (append-to-file "main.go" (goodfunc (str "good" i)))
    (gofmt "main.go")
    (add "main.go")
    (commit (format "%d - a good commit to master - adding a func" i)))


  (checkout :develop)
  (create-branch :feature)
  (doseq [i (range 1 2)]
    (append-to-file "main.go" (badfunc (str "bad" i)))
    (gofmt "main.go")
    (add "main.go")
    (commit (format "%d - a BAD commit to feature - nested ifs" i)))
  ; make sure the git graph is clear, move master ahead abit
  (checkout :master)
  (doseq [i (range 1 3)]
    (append-to-file "main.go" (goodfunc (str "good" i)))
    (gofmt "main.go")
    (add "main.go")
    (commit (format "%d - a good commit to master - adding a func" i)))

  ; check the log and later graph
  (checkout :master)
  (println (:out (sh "git" "lgg"))))

(comment (delete-git-repo!))
(def branches #{:develop :master :feature})
(map create-branch branches)
(checkout :master)

