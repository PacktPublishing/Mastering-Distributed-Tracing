package people

import (
	"database/sql"
	"log"

	_ "github.com/go-sql-driver/mysql"

	"github.com/PacktPublishing/Mastering-Distributed-Tracing/Chapter04/go/lib/model"
)

const dburl = "root:mysqlpwd@tcp(127.0.0.1:3306)/chapter04"

// Repository retrieves information about people.
type Repository struct {
	db *sql.DB
}

// NewRepository creates a new Repository backed by MySQL database.
func NewRepository() *Repository {
	db, err := sql.Open("mysql", dburl)
	if err != nil {
		log.Fatal(err)
	}
	err = db.Ping()
	if err != nil {
		log.Fatalf("Cannot ping the db: %v", err)
	}
	return &Repository{
		db: db,
	}
}

// GetPerson tries to find the person in the database by name.
// If not found, it still returns a Person object with only name
// field populated.
func (r *Repository) GetPerson(name string) (model.Person, error) {
	query := "select title, description from people where name = ?"
	rows, err := r.db.Query(query, name)
	if err != nil {
		return model.Person{}, err
	}
	defer rows.Close()

	for rows.Next() {
		var title, descr string
		err := rows.Scan(&title, &descr)
		if err != nil {
			return model.Person{}, err
		}
		return model.Person{
			Name:        name,
			Title:       title,
			Description: descr,
		}, nil
	}
	return model.Person{
		Name: name,
	}, nil
}

// Close calls close on the underlying db connection.
func (r *Repository) Close() {
	r.db.Close()
}
